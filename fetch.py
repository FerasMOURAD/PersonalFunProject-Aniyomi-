import urllib.request
from bs4 import BeautifulSoup
import sys

url = "https://cinegram.tv/movies?page=1"
req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
try:
    html = urllib.request.urlopen(req).read()
    soup = BeautifulSoup(html, "html.parser")
    items = soup.select("a[href*='/movie/'], a[href*='/tv/']")
    for item in items[:5]:
        print("TAG:", item.parent.name, item.parent.attrs)
        print("A-TAG CLASS:", item.get('class'))
        print("IMG:", item.select_first("img"))
        print("---")
except Exception as e:
    print(e)
