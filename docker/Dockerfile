FROM ubuntu:vivid

RUN apt-get update

RUN apt-get install -y openssh-server eclipse-gef eclipse-egit git swi-prolog

# fonts with all the necessary Unicode symbols
RUN apt-get install -y fonts-droid light-themes

# Web development
RUN apt-get install -y eclipse-wtp-webtools

# sshd configuration
RUN mkdir /var/run/sshd
RUN sed -ri 's/UsePAM yes/#UsePAM yes/g' /etc/ssh/sshd_config
EXPOSE 22

# Install the Cedalion plugin
RUN mkdir -p /usr/share/eclipse/dropins/plugins
COPY net.nansore.cedalion_*.jar /usr/share/eclipse/dropins/plugins
COPY net.nansore.cedalion.prefs /home/cedalion/workspace/.metadata/.plugins/org.eclipse.core.runtime/.settings/

# Create user cedalion
RUN mkdir -p /home/cedalion/workspace /home/cedalion/git
RUN echo cedalion:cedalion:1000:1000::/home/cedalion:/bin/bash | newusers
RUN chown -R cedalion:cedalion /home/cedalion

# Work volumes
USER cedalion
VOLUME /home/cedalion/workspace
VOLUME /home/cedalion/git
WORKDIR /home/cedalion/workspace

USER root
CMD    ["/usr/sbin/sshd", "-D"]
