#Java compiler , Java Virtual Machine , all .java files,
#classes contains all .java files but with .class instead
JAVAC=javac
JVM=java
LIB=lib/jade.jar:classes
CLASSES=classes
sources = $(shell find $(SOURCEDIR) -name '*.java')
classes = $(sources:.java=.class)

#default entry point
default: all

#builds all files
all: $(classes)

#removes .class files
clean:
	@rm -rf $(CLASSES)


#what happens when trying to make a .class file
#@ hides the commands from console (they are not printed)
%.class : %.java
	@$(JAVAC) -cp $(LIB) $< -d $(CLASSES)
