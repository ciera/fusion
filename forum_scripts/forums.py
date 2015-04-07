import scrape
import sys

authorsFile = open(sys.argv[1], 'r')
keywordsFile = open(sys.argv[2], 'r')
lastDate = sys.argv[3]

authors = authorsFile.readlines()
keywords = keywordsFile.readlines()

for author in authors:
    for keyword in keywords:
        author = author.strip()
        keyword = keyword.strip()
        print author, keyword
        scrape.scrapeSearch(author, keyword, lastDate)

keywordsFile.close()
authorsFile.close()
