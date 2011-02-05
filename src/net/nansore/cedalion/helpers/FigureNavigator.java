package net.nansore.cedalion.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.IFigure;

/**
 * An object of this class represents a tree rooted at a single IFigure, and allows navigation on it.
 * It represents the tree as an array of BEGIN and END events comprising a traverse of the tree, and provide some navigation operations
 * such as looking for a container, a contained object, or finding a nesting level.
 *
 */
public class FigureNavigator {
	/**
	 * An abstract class providing a stop criterion for navigation 
	 */
	public abstract class NavigationCriterion {

		public abstract boolean stopOn(Event event) throws FigureNotFoundException;
		

	}
	enum EventType { BEGIN, END }
	/**
	 * A structure representing a single event in the event list 
	 */
	private class Event {
		/**
		 * The figure to which the event applies
		 */
		public IFigure figure;
		/**
		 * Whether this is the BEGINning or END of this figure
		 */
		public EventType eventType;
		/**
		 * The index of the corresponding END or BEGIN event
		 */
		public int peerIndex;
		
		/**
		 * The nesting level of the event. The root events have 0, direct children have 1, etc.
		 */
		public int depth;
	}
	
	/**
	 * The even list, containing the traversal events for all the root's sub-figures
	 */
	private List<Event> eventList = new ArrayList<FigureNavigator.Event>();
	
	/**
	 * A map for finding the BEGIN event for a given figure
	 */
	private Map<IFigure, Integer> figureEvent = new HashMap<IFigure, Integer>();
	
	/**
	 * A registry for navigators for root figures
	 */
	private static Map<IFigure, FigureNavigator> navigatorRegistry = new HashMap<IFigure, FigureNavigator>();

	private IFigure root;
	
	/**
	 * Constructs a navigator based rooted at a figure
	 * @param root the root figure
	 */
	public FigureNavigator(IFigure root) {
		navigatorRegistry.put(root, this);
		this.root = root;
		refresh();
	}
	public void refresh() {
		eventList.clear();
		addEvents(root, 0);
		//dumpEvents();
	}
	private void dumpEvents() {
		for(Event event : eventList) {
			for(int i = 0; i < event.depth; i++)
				System.out.print("  ");
			System.out.println(event.eventType + ": " + event.figure);
		}
	}
	/**
	 * Recursively, add the given figure and all its children to the event list
	 * @param figure the figure to be added
	 */
	private void addEvents(IFigure figure, int depth) {
		Event begin = new Event();
		begin.eventType = EventType.BEGIN;
		begin.figure = figure;
		begin.depth = depth;
		Event end = new Event();
		end.eventType = EventType.END;
		end.figure = figure;
		end.depth = depth;
		
		end.peerIndex = eventList.size();
		figureEvent.put(figure, eventList.size());
		eventList.add(begin);
		
		for(Object child : figure.getChildren()) {
			addEvents((IFigure)child, depth+1);
		}

		begin.peerIndex = eventList.size();
		eventList.add(end);
	}
	
	/**
	 * Find the next index that matches the given criterion
	 * @param startIndex The index to start looking
	 * @param criterion An object providing the stop criterion
	 * @return The first index on which the criterion is true 
	 * @throws FigureNotFoundException if no matching index was found
	 */
	public int navigateForwards(int startIndex, NavigationCriterion criterion) throws FigureNotFoundException {
		int index = startIndex;
		while(index < eventList.size() && !criterion.stopOn(eventList.get(index))) {
			index++;
		}
		if(index == eventList.size())
			throw new FigureNotFoundException();
		return index;
	}

	/**
	 * Find the previous index that matches the given criterion
	 * @param startIndex The index to start looking
	 * @param criterion An object providing the stop criterion
	 * @return The first index (going backwards) on which the criterion is true 
	 * @throws FigureNotFoundException if no matching index was found
	 */
	public int navigateBackwards(int startIndex, NavigationCriterion criterion) throws FigureNotFoundException {
		int index = startIndex;
		while(index >= 0 && !criterion.stopOn(eventList.get(index))) {
			index--;
		}
		if(index == 0)
			throw new FigureNotFoundException();
		return index;
	}
	
	/**
	 * Returns the nesting level of the given figure
	 * @param figure the figure for which we want the nesting level
	 * @return the nesting level
	 * @throws FigureNotFoundException If the figure is not in the tree
	 */
	public int getNestingLevel(IFigure figure) throws FigureNotFoundException {
		Integer index = beginIndex(figure);
		return eventList.get(index).depth;
	}
	
	/**
	 * Returns the next sibling of a figure, in a certain nesting level
	 * @param start The figure to start with
	 * @param nestingLevel The nesting level of the wanted sibling 
	 * @return The next sibling on the desired nesting level 
	 * @throws FigureNotFoundException If there is no next sibling on the desired level
	 */
	public IFigure getNextSibling(IFigure start, final int nestingLevel) throws FigureNotFoundException {
		int result = navigateForwards(endIndex(start), new NavigationCriterion() {
			
			@Override
			public boolean stopOn(Event event) throws FigureNotFoundException {
				if(event.depth < nestingLevel)
					throw new FigureNotFoundException();	// Passed the desired container
				return event.depth == nestingLevel && event.eventType == EventType.BEGIN;
			}
		});
		return eventList.get(result).figure;
	}
	/**
	 * Returns the previous sibling of a figure, in a certain nesting level
	 * @param start The figure to start with
	 * @param nestingLevel The nesting level of the wanted sibling 
	 * @return The previous sibling on the desired nesting level 
	 * @throws FigureNotFoundException If there is no previous sibling on the desired level
	 */
	public IFigure getPrevSibling(IFigure start, final int nestingLevel) throws FigureNotFoundException {
		int result = navigateBackwards(beginIndex(start), new NavigationCriterion() {
			
			@Override
			public boolean stopOn(Event event) throws FigureNotFoundException {
				if(event.depth < nestingLevel)
					throw new FigureNotFoundException();	// Passed the desired container
				return event.depth == nestingLevel && event.eventType == EventType.END;
			}
		});
		return eventList.get(result).figure;
	}
	
	/**
	 * Returns the first descendant of the given figure, that matches the given class
	 * @param start the given figure
	 * @param classToFind the given class
	 * @return The first descendant
	 * @throws FigureNotFoundException if no such descendant exists
	 */
	public IFigure getFirstDescendant(final IFigure start, final Class<? extends IFigure> classToFind, final boolean includeSelf) throws FigureNotFoundException {
		final int nestingLevel = getNestingLevel(start);
		int startIndex = beginIndex(start);
		if(!includeSelf)
			startIndex++;
		int index = navigateForwards(startIndex, new NavigationCriterion() {
			
			@Override
			public boolean stopOn(Event event) throws FigureNotFoundException {
				if(event.depth <= nestingLevel && !(includeSelf && event.figure == start))
					throw new FigureNotFoundException();
				return classToFind.isInstance(event.figure);
			}
		});
		return eventList.get(index).figure;
	}

	
	/**
	 * Returns the last descendant of the given figure, that matches the given class
	 * @param start the given figure
	 * @param classToFind the given class
	 * @return The last descendant
	 * @throws FigureNotFoundException if no such descendant exists
	 */
	public IFigure getLastDescendant(IFigure start, final Class<? extends IFigure> classToFind) throws FigureNotFoundException {
		final int nestingLevel = getNestingLevel(start);
		int index = navigateBackwards(endIndex(start) - 1, new NavigationCriterion() {
			
			@Override
			public boolean stopOn(Event event) throws FigureNotFoundException {
				if(event.depth <= nestingLevel)
					throw new FigureNotFoundException();
				return classToFind.isInstance(event.figure);
			}
		});
		return eventList.get(index).figure;
	}
	
	/**
	 * Just like getFirstDescendant(), only that it finds the smallest matching figure.
	 * @param start
	 * @param classToFind
	 * @return
	 * @throws FigureNotFoundException
	 */
	public IFigure getSmallestDecscendant(IFigure start, Class<? extends IFigure> classToFind) throws FigureNotFoundException {
		IFigure descendant = start;
		try {
			while(true) {
				descendant = getFirstDescendant(descendant, classToFind, false);
			}
		} catch (FigureNotFoundException e) {
			// We found the smallest descendant, i.e., the one that does not have children of type classToFind.
		}
		if(!(classToFind.isInstance(descendant)))
			throw new FigureNotFoundException();
		return descendant;
	}
	
	/**
	 * Find the next sibling of the given figure, that has a common ancestor of the given class.
	 * Unlike  getNextSibling(), it will try with higher and higher ancestors until a sibling is found.
	 * @param start the given figure
	 * @param parentClass the class of the common ancestor
	 * @return the next sibling.
	 * @throws FigureNotFoundException if no such sibling was found
	 */
	public IFigure getNextSiblingOfType(IFigure start, Class<? extends IFigure> parentClass) throws FigureNotFoundException {
		for(IFigure ancesstor = start.getParent(); ancesstor != null; ancesstor = ancesstor.getParent()) {
			if(!parentClass.isInstance(ancesstor)) {
				continue;
			}
			
			try {
				return getNextSibling(start, getNestingLevel(ancesstor) + 1);
			} catch (FigureNotFoundException e) {
				// Not found in this iteration, trying again on the next iteration with a higher ancestor
			}
		}
		// We haven't found anything - need to throw...
		throw new FigureNotFoundException();
	}
	
	/**
	 * Find the previous sibling of the given figure, that has a common ancestor of the given class.
	 * Unlike  getPrevSibling(), it will try with higher and higher ancestors until a sibling is found.
	 * @param start the given figure
	 * @param parentClass the class of the common ancestor
	 * @return the previous sibling.
	 * @throws FigureNotFoundException if no such sibling was found
	 */
	public IFigure getPrevSiblingOfType(IFigure start, Class<? extends IFigure> parentClass) throws FigureNotFoundException {
		for(IFigure ancesstor = start.getParent(); ancesstor != null; ancesstor = ancesstor.getParent()) {
			if(!parentClass.isInstance(ancesstor)) {
				continue;
			}
			
			try {
				return getPrevSibling(start, getNestingLevel(ancesstor) + 1);
			} catch (FigureNotFoundException e) {
				// Not found in this iteration, trying again on the next iteration with a higher ancestor
			}
		}
		// We haven't found anything - need to throw...
		throw new FigureNotFoundException();
	}
	
	public IFigure getFirstDescendantOfNextSibling(IFigure start, Class<? extends IFigure> commonParent, Class<? extends IFigure> descendantType) throws FigureNotFoundException {
		for(IFigure fig = getNextSiblingOfType(start, commonParent); true; fig = getNextSiblingOfType(fig, commonParent)) {
			try {
				return getFirstDescendant(fig, descendantType, true);
			} catch (FigureNotFoundException e) {
				// Try on the next iteration...
			}
		}
	}
	
	public IFigure getFirstDescendantOfPrevSibling(IFigure start, Class<? extends IFigure> commonParent, Class<? extends IFigure> descendantType) throws FigureNotFoundException {
		for(IFigure fig = getPrevSiblingOfType(start, commonParent); true; fig = getPrevSiblingOfType(fig, commonParent)) {
			try {
				return getFirstDescendant(fig, descendantType, true);
			} catch (FigureNotFoundException e) {
				// Try on the next iteration...
			}
		}
	}
	
	
	
	private int endIndex(IFigure fig) throws FigureNotFoundException {
		return beginEvent(fig).peerIndex;
	}
	private Event beginEvent(IFigure fig) throws FigureNotFoundException {
		return eventList.get(beginIndex(fig));
	}
	private int beginIndex(IFigure fig) throws FigureNotFoundException {
		Integer index = figureEvent.get(fig);
		if(index == null)
			throw new FigureNotFoundException();
		return index;
	}
	public static FigureNavigator getNavigatorForRoot(IFigure root) {
		FigureNavigator nav = navigatorRegistry.get(root);
		if(nav != null)
			return nav;
		else
			return new FigureNavigator(root);
	}
	public IFigure getAncestor(IFigure start, Class<? extends IFigure> ancestorClass) throws FigureNotFoundException {
		for(IFigure fig = start; fig != null; fig = fig.getParent()) {
			if(ancestorClass.isInstance(fig))
				return fig;
		}
		throw new FigureNotFoundException();
	}
}
