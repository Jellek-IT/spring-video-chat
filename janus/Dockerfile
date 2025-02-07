FROM debian:bullseye-slim

# Modified by Maciej Bronikowski
# Start
WORKDIR /usr/local/src
# End

RUN apt-get -y update && \
	apt-get install -y \
		libavutil-dev \
		libavformat-dev \
		libavcodec-dev \
		libmicrohttpd-dev \
		libjansson-dev \
		libssl-dev \
		libsofia-sip-ua-dev \
		libglib2.0-dev \
		libopus-dev \
		libogg-dev \
		libcurl4-openssl-dev \
		liblua5.3-dev \
		libconfig-dev \
		libusrsctp-dev \
		libwebsockets-dev \
		libnanomsg-dev \
		librabbitmq-dev \
		pkg-config \
		gengetopt \
		libtool \
		automake \
		build-essential \
		wget \
		git \
		gtk-doc-tools && \
	apt-get clean && \
	rm -rf /var/lib/apt/lists/*

RUN cd /tmp && \
	wget https://github.com/cisco/libsrtp/archive/v2.3.0.tar.gz && \
	tar xfv v2.3.0.tar.gz && \
	cd libsrtp-2.3.0 && \
	./configure --prefix=/usr --enable-openssl && \
	make shared_library && \
	make install

RUN cd /tmp && \
	git clone https://gitlab.freedesktop.org/libnice/libnice && \
	cd libnice && \
	git checkout 0.1.17 && \
	./autogen.sh && \
	./configure --prefix=/usr && \
	make && \
	make install

# Modified by Maciej Bronikowski
# Start
RUN cd /usr/local/src && git clone https://github.com/meetecho/janus-gateway.git && cd ./janus-gateway && \
    git checkout refs/tags/v1.2.4

# Copy modifications to videoroom plugin
COPY ./plugin/janus_videoroom.c /usr/local/src/janus-gateway/src/plugins

RUN cd /usr/local/src/janus-gateway && sh autogen.sh &&  \
    ./configure --prefix=/usr/local \
	--enable-post-processing \
	--disable-data-channels \
    --disable-rabbitmq \
    --disable-mqtt \
    --disable-unix-sockets && \
    make && make install && make configs
# End

FROM debian:bullseye-slim

RUN apt-get -y update && \
	apt-get install -y \
		libmicrohttpd12 \
		libavutil-dev \
		libavformat-dev \
		libavcodec-dev \
		libjansson4 \
		libssl1.1 \
		libsofia-sip-ua0 \
		libglib2.0-0 \
		libopus0 \
		libogg0 \
		libcurl4 \
		liblua5.3-0 \
		libconfig9 \
		libusrsctp1 \
		libwebsockets16 \
		libnanomsg5 \
		librabbitmq4 && \
	apt-get clean && \
	rm -rf /var/lib/apt/lists/*

COPY --from=0 /usr/lib/libsrtp2.so.1 /usr/lib/libsrtp2.so.1
RUN ln -s /usr/lib/libsrtp2.so.1 /usr/lib/libsrtp2.so

COPY --from=0 /usr/lib/libnice.la /usr/lib/libnice.la
COPY --from=0 /usr/lib/libnice.so.10.10.0 /usr/lib/libnice.so.10.10.0
RUN ln -s /usr/lib/libnice.so.10.10.0 /usr/lib/libnice.so.10
RUN ln -s /usr/lib/libnice.so.10.10.0 /usr/lib/libnice.so

COPY --from=0 /usr/local/bin/janus /usr/local/bin/janus
COPY --from=0 /usr/local/bin/janus-pp-rec /usr/local/bin/janus-pp-rec
COPY --from=0 /usr/local/bin/janus-cfgconv /usr/local/bin/janus-cfgconv
COPY --from=0 /usr/local/etc/janus /usr/local/etc/janus
COPY --from=0 /usr/local/lib/janus /usr/local/lib/janus
COPY --from=0 /usr/local/share/janus /usr/local/share/janus

EXPOSE 8188
EXPOSE 7088
EXPOSE 10000-10200/udp

# Modified by Maciej Bronikowski
# Start
ARG TOKEN_AUTH_SECRET
ARG ADMIN_SECRET
ARG VIDEOROOM_ADMIN_KEY

COPY ./config /opt/app/config

RUN bash -c 'sed -i "s~\${TOKEN_AUTH_SECRET}~${TOKEN_AUTH_SECRET}~g" /opt/app/config/janus.jcfg'
RUN bash -c 'sed -i "s~\${ADMIN_SECRET}~${ADMIN_SECRET}~g" /opt/app/config/janus.jcfg'
RUN bash -c 'sed -i "s~\${VIDEOROOM_ADMIN_KEY}~${VIDEOROOM_ADMIN_KEY}~g" /opt/app/config/janus.plugin.videoroom.jcfg'

CMD /usr/local/bin/janus -F /opt/app/config --nat-1-1=$NAT_1_1
# End