
#Crawler4j是使用JAVA开发的开源Web爬虫

###Crawler4j通过配置文件配置抓取任务,然后使用多线程进行抓取的Web爬虫.每个抓取任务使用独立线程上下文,支持在配置文件中同时配置多个抓取任务,复杂的抓取任务可通过扩展框架提供的基类实现,可以方便的将爬虫和其他解析存储程序进行集成.

> ####使用方法请参考 crawler4j-simple 模块

##It's composed of two parts:

1. crawler4j-core: crawler4j core module. 

2. crawler4j-simple: a simple WEB crawler implementation base on crawler4j-core.


================================================================
Quick Start
================================================================

0.Install the git and maven command line:

    yum install git
    or: apt-get install git
    
    cd ~
    wget http://www.apache.org/dist/maven/maven-3/3.0.4/binaries/apache-maven-3.0.4-bin.tar.gz
    tar zxvf apache-maven-3.0.4-bin.tar.gz
    vi .bash_profile
    - edit: export PATH=$PATH:~/apache-maven-3.0.4/bin
    source .bash_profile

1.Checkout the crawler4j source code:

    cd ~
    git clone https://github.com/zhuoran/crawler4j.git
    cd crawler4j
    git checkout -b crawler4j-0.1
    git checkout master 

2.Import the source code to eclipse project:

    cd crawler4j/crawler4j-simple
    mvn eclipse:eclipse
    Eclipse -> Menu -> File -> Import -> Exsiting Projects to Workspace -> Browse -> Finish
    
    Edit Config:
    crawler4j-simple/src/main/resources/crawler4j.xml
    
3.Build the binary package:

    cd ~/crawler4j
    mvn clean install -Dmaven.test.skip
    ll

4.Install the crawler4j simple demo:

    cd ~/crawler4j/crawler4j-simple/target
    tar zxvf crawler4j-simple-0.1-assembly.tar.gz
    cd crawler4j-simple-0.1/bin
    ./start.sh
    

================================================================
Configuration
================================================================

Crawler Config Example : 
>1. See crawler4j-simple/src/main/resources/crawler4j.xml
>2. See [Jsoup selector-syntax](http://jsoup.org/cookbook/extracting-data/selector-syntax)
>3. You can configure multiple crawling tasks in crawler4j.xml
     
     <site>
        <charset>UTF-8</charset> 
        <name>Infoq-News</name>
        <delay>1000</delay>
        <url><![CDATA[http://www.infoq.com/infoq.action?newsidx=10]]></url>
        <extract_links_elementId>div.box-content-3</extract_links_elementId>
        <parser>me.zhuoran.crawler4j.simple.ParserDemo</parser>
        <crawler>me.zhuoran.crawler4j.simple.InfoqCrawler</crawler>
     </site>


[charset] For target html page charset,like UTF-8 GBK .   
[name] Task name or thread name.  
[delay] Each fetch of a html page delay some time, unit is ms.  
[url] URL can be list page url or target page url.  
URL can contain one or more list page url must use "," split.  
URL can contain one or more target page url must use "|" split.  
[extract_links_elementId] This tag can be a regex string or a jsoup query.  
[parser] This is class full name of parser.  
[crawler] This is class full name of cralwer.  


================================================================
Feature
================================================================
1. 支持更多类型数据自动识别
2. 完善HttpClient部分代码,支持更多抓取模式,支持gzip等格式解析
3. 重构并优化架构

For more, please refer to:

    [Wiki](http://www.zhuoran.me/crawler4j/wiki) 准备中

================================================================
Support
================================================================

* Email: zoran.wang@gmail.com
* Twitter: @lopor
* 新浪微博: @王小然


