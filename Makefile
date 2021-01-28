### Makefile

PWD=$(shell pwd)
JAR=$(shell basename $(shell ls $(PWD)/target/*.jar))
help:
	@echo "PWD: $(PWD)"
	@echo "JAR: $(JAR)"
clean compile package:
	@docker run -it --rm -v "$(PWD)":/home/devel -v "$(HOME)/.m2":/root/.m2 -w /home/devel --add-host 'consul-node:192.168.3.51' extvos/maven mvn $@ -Dmaven.test.skip=true
	
run:
	@docker run -it --rm -v "$(PWD)/target":/home/target -p 8080:8080 -w /home/target extvos/maven java -jar $(JAR)
