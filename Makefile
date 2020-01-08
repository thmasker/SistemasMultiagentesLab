
ifdef OS
   RM = del /Q /S
   SRC_FILES := $(shell forfiles /s /p .\src /m *.java /c "cmd /c echo @path")
   OS_SEP = \\
else
   ifeq ($(shell uname), Linux)
      RM = rm -rf
	  SRC_FILES := $(shell find ./src -name "*.java")
	  OS_SEP = /
   endif
endif


BUILD_DIR = .$(OS_SEP)build
BOTTEST_CLASS := movietool.test.BotTest
INTERFACE_CLASS := movietool.InterfaceAgent
CLASSPATH := ".$(OS_SEP)lib$(OS_SEP)jsoup-1.12.1.jar;.$(OS_SEP)lib$(OS_SEP)jade.jar"


all: java

$(BUILD_DIR):
	@IF NOT EXIST "$(BUILD_DIR)" (mkdir "$(BUILD_DIR)")

java: $(BUILD_DIR)
	javac -cp $(CLASSPATH) -d "$(BUILD_DIR)" $(SRC_FILES)

test-bots:
	java -cp $(CLASSPATH);$(BUILD_DIR) $(BOTTEST_CLASS)

run:
	java -cp $(BUILD_DIR);$(CLASSPATH) jade.Boot -gui -agents "interface:$(INTERFACE_CLASS)"

clean:
	$(RM) $(BUILD_DIR) .$(OS_SEP)APDescription.txt .$(OS_SEP)MTPs-Main-Container.txt