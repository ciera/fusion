import urllib2
import urllib
import string
import json
from BeautifulSoup import BeautifulSoup

def scrape():
    opener = urllib2.build_opener(urllib2.HTTPCookieProcessor())
    urllib2.install_opener(opener)
    login(opener)
    getAllUserData(br)

def login(opener):
    url = 'https://login.asp.net/login/signin.aspx'

    response = opener.open(url)
    html = response.read()
    response.close()
    soup = BeautifulSoup(html)


    hiddens = soup.findAll('input', attrs={'type':'hidden'})
    values = {}
    for hidden in hiddens:
        values[str(hidden['name'])] = str(hidden['value'])

    
    values['__EVENTTARGET'] = 'ctl00$MainContent$loginForm$LinkButton1'
    values['__EVENTARGUMENT'] = ''

    data = urllib.urlencode(values)
    response = opener.open(url, data)
    html = response.read()
    response.close()
 
    hiddens2 = soup.findAll('input', attrs={'type':'hidden'})
    values2 = {}
    for hidden2 in hiddens2:
        values2[str(hidden2['name'])] = str(hidden2['value'])
    values2['ctl00$MainContent$loginForm$txtUsername'] = 'ciera'
    values2['ctl00$MainContent$loginForm$txtPassword'] = 'fzzybnny'
    values2['ctl00$MainContent$loginForm$btnLogin'] = 'Sign In'
    values2['__EVENTTARGET'] = ''
    values2['__EVENTARGUMENT'] = ''
    values2['__LASTFOCUS'] = ''

    try:
       data2 = urllib.urlencode(values2)
       print data2
       request = urllib2.Request('https://login.asp.net/login/signin.aspx')
       request.add_header('Accept','*/*')       
       response2 = opener.open(request, data2)
       html2 = response2.read()
       print html2
       if 'Signed In As' in html:
           print 'LOGGED IN AS ciera'
       response.close()
    except urllib2.HTTPError, e:
       print '************GOT AN HTTP ERROR**************'
       print e.code
       print e.msg
       print e.headers
       print e.fp.read()

def getAllUserData(opener):
    page = 0
    count = 5
    url = 'http://www.asp.net/community/recognition/hall-of-fame/soa/core/user.svc/gettopleveluserspaged?count='+str(count)+'&level=-1&order=0&page='

    response = opener.open(url + str(page))
    rawData = response.read()
    response.close()

    results = json.loads(rawData)
    data = json.loads(results['Data'])
    print data['TotalUsers']
    processResultsPage(opener, data)

def processResultsPage(opener, results):
    for user in results['Users']:
       processUser(opener, user['Username'])

def processUser(opener, userName):
    url = 'http://forums.asp.net/members/' + userName + '.aspx'
    print url
    response = opener.open(url)
    html = response.read()
    response.close()

    soup = BeautifulSoup(html)
    profileTable = soup.find('table', attrs={'class':'tbl-profile-details'})
    postsRow = profileTable.tbody.contents[6]
    print postsRow

if __name__ == "__main__":
    scrape();
