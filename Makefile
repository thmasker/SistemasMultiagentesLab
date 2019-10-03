

SRCDIR = .\src
BUILDDIR := .\build
MAINCLASS := BotTest
CLASSPATH := "lib/jsoup-1.12.1.jar"


all: java

$(BUILDDIR):
	@IF NOT EXIST "$(BUILDDIR)" (mkdir "$(BUILDDIR)")

java: $(BUILDDIR)
	javac -cp $(CLASSPATH) -d "$(BUILDDIR)" $(SRCDIR)\*.java
	
run:
	java -cp $(CLASSPATH);$(BUILDDIR) BotTest

clean:
	del /Q $(BUILDDIR)