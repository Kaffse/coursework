import requests as r
import string
from BeautifulSoup import BeautifulSoup as soup

#url = 'http://dl.fedoraproject.org/pub/fedora/linux/releases/20/'
url = 'http://dl.fedoraproject.org/pub/fedora/linux/releases/20/Everything/x86_64/os/Packages/'

for c in string.ascii_lowercase:
    html = soup(r.get(url + c).text)
    for tag in html.findAll('a', {'href': True}):
        dir_name = tag.attrMap['href']
        file = r.head(url + c + '/' + dir_name)
        if 'content-length' in file.headers:
            size = file.headers['content-length']
            print dir_name + " " + size
