from BeautifulSoup import BeautifulSoup, NavigableString
import urllib2
import urllib
import time
import hashlib
import string
from datetime import datetime, date, time



def scrapeSearch(author, keyword, lastDate):
    opener = urllib2.build_opener(urllib2.HTTPCookieProcessor())
    urllib2.install_opener(opener)
    login(opener)
    lastDateOfInitPost = datetime.strptime(lastDate, "%b %d, %Y")
    runSearch(opener, author, keyword, lastDateOfInitPost)


def scrapeSingleThread(id, lastDate):
    opener = urllib2.build_opener(urllib2.HTTPCookieProcessor())
    urllib2.install_opener(opener)
    login(opener)
    url='http://forum.springsource.org/showthread.php?t=' + str(id)
    lastDateOfInitPost = datetime.strptime(lastDate, "%b %d, %Y")
    check(opener, url, lastDateOfInitPost)

    

def getPostData(postContents):
    postData = {}
    postData['id'] = postContents['id'][4:]
    bigUserName = postContents.find('a', attrs={'class':'bigusername'})
    if bigUserName is None:
        postData['poster'] = 'Guest'
    else:
        postData['poster'] = bigUserName.contents[0]
    strDate = postContents.find('a', attrs={'name':'post'+postData['id']}).nextSibling.strip()
    strDate = string.replace(strDate, "st", "")
    strDate = string.replace(strDate, "nd", "")
    strDate = string.replace(strDate, "rd", "")
    strDate = string.replace(strDate, "th", "")

    postData['date'] = datetime.strptime(strDate, "%b %d, %Y, %I:%M %p")
    contentsID = 'post_message_' + postData['id']
    postData['contents'] = postContents.find('div', attrs={'id':contentsID})

    return postData


def getLowContents(contentList, lowString):
    for item in contentList:
        if isinstance(item, NavigableString):
            lowString = lowString + ' ' + item.lower()
        else:
            lowString = getLowContents(item.contents, lowString)
    return lowString


def OPAffirms(thread):
    opName = thread[0]['poster']
    for i in range(1,len(thread)):
        if (opName == thread[i]['poster']):
            lowContents = getLowContents(thread[i]['contents'].contents, '')
            if ('solved' in lowContents):
                return True
            if ('that work' in lowContents):
                return True
            if ('works' in lowContents):
                return True
            if ('working' in lowContents):
                return True
    else:
        return False
     

def HasCodeTag(thread):
    for i in range(0,len(thread)):
        contents = thread[i]['contents']
        tags = contents.findAll('pre', attrs={'class':'alt2'})
        if len(tags) > 0:
            return True
    else:
        return False

def getPagePosts(soup, threadURL):
   posts = soup.find('div', attrs={'id':'posts'}).findAll('table',attrs={'class':'tborder'})
   
   postData = []
   for i in range(0,len(posts)):
      data = getPostData(posts[i])
      data['url'] = threadURL
      postData.append(data)

   return postData


def check(opener, threadURL, lastDate):
    print threadURL
#Get data from the first page
    tResponse = opener.open(threadURL)
    threadContents = tResponse.read()
    soup = BeautifulSoup(threadContents)
    postData = getPagePosts(soup, threadURL)
    totalContents = str(soup.find('div', attrs={'id':'posts'}))
    tResponse.close()

    if postData[0]['date'] > lastDate:
        print 'Wrong date' 
        return

#Get data from any remaining pages
    pages = soup.find('div', attrs={'class':'pagenav'})
    if pages is not None:
        pages = pages.find('td', attrs={'class':'vbmenu_control','style':'font-weight:normal'})
        totalPages = pages.contents[0].split()[3]
        for i in range(2, int(totalPages) + 1):
            nextURL = threadURL + '&page=' + str(i)
            tResponse = opener.open(nextURL)
            threadContents = tResponse.read()
            soup = BeautifulSoup(threadContents)
            postData.extend(getPagePosts(soup, threadURL))
            totalContents += str(soup.find('div', attrs={'id':'posts'}))
            tResponse.close()
       
#Check it!
    print 'checking', len(postData), 'posts'
    if not HasCodeTag(postData):
        print 'No example'
        return
    elif not OPAffirms(postData):
        print 'No affirmation'
        return
    else:
        print 'Good!'
        f = open('threads/' + threadURL.split('=')[1] + '.html', 'w')
        f.write('<html><body>')
        f.write('<link rel="stylesheet" type="text/css" href="style.css"/>')
        f.write(totalContents)
        f.write('</body></html>')
        f.close()


def login(opener):
    url = 'http://forum.springsource.org/login.php'
    values = {'do':'login',
        'vb_login_username':'ciera',
        'vb_login_password':'fzzybnny',
        'cookieuser':'1',
        's':'',
        'securitytoken':'1292598887-a466ec9eea7aace5c2f0561aa8110d1f9a7eea44',
        'vb_login_md5password':'',
        'vb_login_md5password_utf':''
    }

    data = urllib.urlencode(values)
    response = opener.open(url, data)
    html = response.read()

    if 'Thank you for logging in, ciera.' in html:
       print "LOGGED IN AS ciera"

    response.close()

def runSearch(opener, author, keyword, lastDate):
    url = 'http://forum.springsource.org/search.php'

    response = opener.open(url)
    html = response.read()
    soup = BeautifulSoup(html)
    token = soup.find('form', attrs = {"id":"searchform"})
    token = token.find('input', attrs = {"name":"securitytoken"})
    securitytoken = token['value']
    response.close()

    values = {'do' : 'process',
          's':'',
          'securitytoken':securitytoken,
          'searchthreadid':'',
          'searchuser'  : author,
          'starteronly' : '0',
          'exactname' : '1',
          'query' : keyword,
          'titleonly' : '0',
	  'replyless': '0',
          'replylimit':'2',
          'searchdate':'365',
          'beforeafter':'before',
          'sortby':'lastpost',
          'order':'descending',
          'showposts':'0',
          'tag':'',
          'forumchoice[]':'25',
          'childforums':'1'
         }
    data = urllib.urlencode(values)
    response = opener.open(url, data)
    searchURL = response.geturl()
    hasResults = searchURL.split('=')
    if len(hasResults) == 1:
        return
    searchID = hasResults[1]

    response.close()
    
    resultsURL = 'http://forum.springsource.org/search.php?searchid=' + searchID + '&pp=100'
    response = opener.open(resultsURL)
    html = response.read()
    soup = BeautifulSoup(html)
    response.close()

    processResultsPage(opener, soup, lastDate)

    #get number of pages
    pages = soup.find('div', attrs={'class':'pagenav'})
    if pages is not None:
        pages = pages.find('td', attrs={'class':'vbmenu_control','style':'font-weight:normal'})
        totalPages = pages.contents[0].split()[3]
        for i in range(2, int(totalPages) + 1):
            response = opener.open(resultsURL + '&page=' + str(i))
            html = response.read()
            soup = BeautifulSoup(html)
            processResultsPage(opener, soup, lastDate)
            response.close()



def processResultsPage(opener, soup, lastDate):
    results = soup.find('table', attrs = {"id":"threadslist"})
    results = results.findAll('tr')

    for i in range(2,len(results) - 1):
        cells = results[i].findAll('td')
        id = cells[2]['id'][15:]
        link = 'http://forum.springsource.org/showthread.php?t=' + id
        replies = cells[4].find('a').contents[0]
        forum = cells[6].find('a').contents[0]
        check(opener, link, lastDate)

if __name__ == "__main__":
    import sys
    if len(sys.argv) == 4:
        scrapeSearch(sys.argv[1], sys.argv[2], sys.argv[3])
    else:
        scrapeSingleThread(sys.argv[1], sys.argv[2])
