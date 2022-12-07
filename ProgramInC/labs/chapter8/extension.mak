# Use clang, but could be gcc
CC=clang

# Our 'core' set of flags used for everything
BASEFLAGS=-Wall -Wextra -Wpedantic -std=c99 -Wvla -Wfloat-equal

# Optimise code, but allow no warnings during compilation
PRODFLAGS=$(BASEFLAGS) -O3
# Be super tough - fail to compile if *any* errors
# PRODFLAGS=$(BASEFLAGS) -O1 -Werror

# Not optimisation, add in Sanitizer code -> huge executable file but will tell you if an array goes out-of-bounds etc.
DEVFLAGS=$(BASEFLAGS) -fsanitize=address -fsanitize=undefined -g3 
LDLIBS= -lm

# Production Code - no warnings allowed, no sanitizer
extension: extension.c extension.h
	$(CC) extension.c -o extension $(PRODFLAGS) $(LDLIBS)

# Development Code - sanitizer enabled
extension_dev: extension.c extension.h
	$(CC) extension.c -o extension_dev $(DEVFLAGS) $(LDLIBS)

all: 
	extension extension_dev

clean:
	rm -f extension extension_dev
