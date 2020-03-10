**Custom NiFi processors**

A collection of my NiFi custom processors I've built for various purposes.

If I find myself repeating the same pattern a few times I generally consider building a custom processor.
This means they're not particually generalised, and they're still a work in progress.

#

**nifi-replay-processor**

This processor takes two text inputs, converts the strings to UTF-8, removes BOMs, trims whitespace, and then compares the strings.
It will not remove other non-breaking space characters.

Created after a number of bugs to do with BOMs being part of a string, meaning that two strings that look identical could be different.
