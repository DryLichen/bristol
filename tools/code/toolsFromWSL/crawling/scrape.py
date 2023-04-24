from bs4 import BeautifulSoup
import os

# loop every *.html pages
for file in os.listdir('cattax'):
  if file[-4:] == 'html':
    soup = BeautifulSoup(open('cattax/'+file,'r'), features='html.parser')

    # check if the page is leaf node
    containers = soup.find_all(class_='container')
    if len(containers) == 0:
      print(soup.title.text + " : " + soup.h1.text)

      for info in soup.find_all('p', {'class': 'info'}):
        print(info.contents)
        print('\n')

        # store info content in a dictionary
        emptyDict = dict()
        emptyDict[file[:-4]] = info.contents
