#coding=utf-8
'''
Created on 2017��4��19��

@author: Administrator
'''
from bs4 import BeautifulSoup
import codecs
import urllib2
import re

def crawl(url):
    page=urllib2.urlopen(url)
    contents = page.read()
    soup = BeautifulSoup(contents,"html.parser")
    print u'�����Ӱ250�����\tӰƬ��\t ����\t ��������'
    for tag in soup.find_all(attrs={"class":'item'}):
        ###find_all() ����������ǰtag������tag�ӽڵ�,���ж��Ƿ���Ϲ�����������
        num = tag.find('em').get_text()
        print num
        name = tag.find(attrs={"class":'hd'}).a.get_text()
        link_path = tag.find(attrs={"class":'hd'}).a.get('href')
        print link_path
        name = name.replace("\n",' ')
        print "smile:"+name
        title = tag.find_all(attrs={"class":"title"})  
#         print "sunshine:"+title
        i = 0  
        for n in title:  
            text = n.get_text()  
            text = text.replace('/','')  
            text = text.lstrip()  
            if i==0:  
                print u'[���ı���]', text  
                infofile.write(u"[���ı���]" + text + "\r\n")  
            elif i==1:  
                print u'[Ӣ�ı���]', text  
                infofile.write(u"[Ӣ�ı���]" + text + "\r\n")  
            i = i + 1  
        #��ȡ���ֺ�������  
        info = tag.find(attrs={"class":"star"}).get_text()  
        info = info.replace('\n',' ')  
        info = info.lstrip()  
        print info  
        mode = re.compile(r'\d+\.?\d*')  
        print mode.findall(info)  
        i = 0  
        for n in mode.findall(info):  
            if i==0:  
                print u'[����]', n  
                infofile.write(u"[����]" + n + "\r\n")  
            elif i==1:  
                print u'[����]', n  
                infofile.write(u"[����]" + n + "\r\n")  
            i = i + 1  
        #��ȡ����  
        info = tag.find(attrs={"class":"inq"})  
        if(info): # 132����Ӱ [��ʧ�İ���] û��Ӱ��  
            content = info.get_text()  
            print u'[Ӱ��]', content  
            infofile.write(u"[Ӱ��]" + content + "\r\n")  
        print ''   
         
if __name__ == '__main__':
    infofile = codecs.open(r'f:\result_douban.txt', "w", "utf-8")
    url = 'http://movie.douban.com/top250?format=text'  
    i = 0  
    while i<10:  
        print u'ҳ��', (i+1)  
        num = i*25 #ÿ����ʾ25�� URL��Ű�25����  
        url = 'https://movie.douban.com/top250?start=' + str(num) + '&filter='  
        crawl(url)  
        infofile.write("\r\n\r\n\r\n")  
        i = i + 1  
    infofile.close()  

  
  
# html_doc = """ 
# <html><head><title>The Dormouse's story</title></head> 
# <body> 
# <p class="title"><b>The Dormouse's story</b></p> 
# <p class="story">Once upon a time there were three little sisters; and their names were 
# <a href="http://example.com/elsie" class="sister" id="link1">Elsie</a>, 
# <a href="http://example.com/lacie" class="sister" id="link2">Lacie</a> and 
# <a href="http://example.com/tillie" class="sister" id="link3">Tillie</a>; 
# and they lived at the bottom of a well.</p> 
# <p class="story">...</p> 
# """  
#   
# #��ȡBeautifulSoup���󲢰���׼������ʽ���  
# soup = BeautifulSoup(html_doc)  
# print(soup.prettify())  
    