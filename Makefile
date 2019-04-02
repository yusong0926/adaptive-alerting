.PHONY: all release

PWD := $(shell pwd)
MAVEN := ./mvnw

clean:
	${MAVEN} clean

build:
	${MAVEN} install

all: clean build

docker_build:
	cd modelservice && $(MAKE) docker_build && cd ..
	cd kafka && $(MAKE) docker_build && cd ..

# build all and release
release: all
	cd modelservice && $(MAKE) release && cd ..
	cd kafka && $(MAKE) release && cd ..
	# deploy to Sonatype
	./.travis/deploy.sh
