# fileprocessor

Processes Large File -> Matches -> Aggregates -> Display

## Modules

1. The main module - reads a large text file in parts (e.g. 1000 lines in each part) and
   sends each part (as string) to a matcher. After all matchers completed, it calls the
   aggregator to combine and print the results
2. The matcher - gets a text string as input and searches for matches of a given set of
   strings. The result is a map from a word to its location(s) in the text
3. The aggregator - aggregates the results from all the matchers and prints the results.