#! /bin/sh

#remove old vivio
rm -r vivio.googlecode.com/

#wget new version
wget -r https://vivio.googlecode.com/svn/trunk

#build binary information
ant -buildfile vivio.googlecode.com/svn/trunk/Vivio/build-bin.xml

#build updated jar
ant -buildfile vivio.googlecode.com/svn/trunk/Vivio/build-jar.xml

#cp jar to current directory
cp vivio.googlecode.com/svn/trunk/Vivio/vivio.jar .

#launch jar
java -jar vivio.jar -o Seiyria -c kellyirc

