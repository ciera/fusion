from BeautifulSoup import BeautifulSoup, NavigableString
import urllib2
import urllib
import hashlib
import string


def scrape():
    opener = urllib2.build_opener(urllib2.HTTPCookieProcessor())
    urllib2.install_opener(opener)
    login(opener)
    getAllUserData(opener)

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

def getAllUserData(opener):
    url = 'http://forum.springsource.org/memberlist.php'

    response = opener.open(url)
    html = response.read()
    soup = BeautifulSoup(html)
    response.close()

    processResultsPage(opener, soup,1)

    #get number of pages
    pages = soup.find('div', attrs={'class':'pagenav'})
    if pages is not None:
        pages = pages.find('td', attrs={'class':'vbmenu_control','style':'font-weight:normal'})
        totalPages = pages.contents[0].split()[3]
        print 'Found ' + totalPages + ' pages'
        for i in range(2, int(totalPages) + 1):
            response = opener.open('http://forum.springsource.org/memberlist.php?&order=asc&sort=username&page=' + str(i))
            html = response.read()
            soup = BeautifulSoup(html)
            processResultsPage(opener, soup,i)
            response.close()



def processResultsPage(opener, soup, pageNum):
    results = soup.findAll('td', attrs = {"class":"alt1Active"})

    for i in range(0,len(results) - 1):
        try:
           name = results[i].a.string
           num = results[i].parent.find('td', attrs = {"class":"alt2"}).string
           print name + '\t' + num
        except UnicodeEncodeError:
           print "Unicode problem on page "+ str(pageNum)
           pass

if __name__ == "__main__":
    scrape();
