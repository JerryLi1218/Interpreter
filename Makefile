# Makefile for Lox compiler

# Java compiler
JAVAC = javac

# Java Virtual Machine
JAVA = java

# Source directory
SRC_DIR = lox

# Output directory
OUT_DIR = out

# JAR file name
JAR_NAME = LoxInterpreter.jar

# Get all Java source files
JAVA_FILES = $(wildcard $(SRC_DIR)/*.java)

# Compile Java source files
compile:
	mkdir -p $(OUT_DIR)
	$(JAVAC) -d $(OUT_DIR) $(JAVA_FILES)

# Create executable JAR file
jar:
	jar cvfe $(JAR_NAME) lox.Lox -C $(OUT_DIR) .

# Run Lox Interpreter
run:
	$(JAVA) -jar $(JAR_NAME)

# Clean up compiled files and JAR file
clean:
	rm -rf $(OUT_DIR) $(JAR_NAME)
