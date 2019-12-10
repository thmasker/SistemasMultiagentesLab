
ifdef OS
   RM = del /Q /S
   OS_SEP = \\
else
   ifeq ($(shell uname), Linux)
      RM = rm -rf
	  OS_SEP = /
   endif
endif


SRC_DIR = .$(OS_SEP)src
BUILD_DIR = .$(OS_SEP)build
BOTTEST_CLASS := BotTest
AGENT_CLASS := movietool.InterfaceAgent
CLASSPATH := ".$(OS_SEP)lib$(OS_SEP)jsoup-1.12.1.jar;.$(OS_SEP)lib$(OS_SEP)jade.jar"


all: java

$(BUILD_DIR):
	@IF NOT EXIST "$(BUILD_DIR)" (mkdir "$(BUILD_DIR)")

java: $(BUILD_DIR)
	javac -cp $(CLASSPATH) -d "$(BUILD_DIR)" $(SRC_DIR)$(OS_SEP)*.java
	
test-bots:
	java -cp $(CLASSPATH);$(BUILD_DIR) $(BOTTEST_CLASS)

run:
	java -cp $(BUILD_DIR);$(CLASSPATH) jade.Boot -gui -agents "interface:$(AGENT_CLASS)"

clean:
	$(RM) $(BUILD_DIR)