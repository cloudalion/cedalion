FROM ubuntu:vivid

RUN apt-get update

RUN apt-get install -y openssh-server eclipse-gef eclipse-egit git swi-prolog

# fonts with all the necessary Unicode symbols
RUN apt-get install -y fonts-droid

# sshd configuration
RUN mkdir /var/run/sshd
RUN echo 'root:root' |chpasswd
RUN sed -ri 's/^PermitRootLogin\s+.*/PermitRootLogin yes/' /etc/ssh/sshd_config
RUN sed -ri 's/UsePAM yes/#UsePAM yes/g' /etc/ssh/sshd_config
EXPOSE 22

# Install the Cedalion plugin
RUN mkdir -p /usr/share/eclipse/dropins/plugins
COPY net.nansore.cedalion_*.jar /usr/share/eclipse/dropins/plugins
COPY net.nansore.cedalion.prefs /root/workspace/.metadata/.plugins/org.eclipse.core.runtime/.settings/

# Work volumes
VOLUME /root/workspace
VOLUME /root/git

CMD    ["/usr/sbin/sshd", "-D"]
