server:
	docker build \
		-t addgreen-fileupload:0.0.1  .\
		--build-arg PACKAGE_NAME=addgreen-fileupload \
		--build-arg VCS_REF=`git rev-parse HEAD` \
		--build-arg BUILD_DATE=`date -u +"%Y-%m-%dT%H:%M:%SZ"` \

run:
	docker-compose up

rm-server:
	docker-compose rm

rm-image:
	docker rmi -f rmi $$(docker images -aq)

prune-ser:
	docker system prune

prune-vol:
	docker volume prune
