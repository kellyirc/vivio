#! /bin/sh

echo "wgetting new version."
#wget new version
wget -r https://vivio.googlecode.com/svn/trunk

if [ $? -ne 0 ]; then
	echo "Failed to wget new version. Aborting."
	exit 1
fi

echo "Building new binary files."
#build binary information
ant -buildfile vivio.googlecode.com/svn/trunk/Vivio/build-bin.xml

if [ $? -ne 0 ]; then
	echo "Failed to build new binary. Aborting."
	exit 1
fi

echo "Building new jar."
#build updated jar
ant -buildfile vivio.googlecode.com/svn/trunk/Vivio/build-jar.xml

if [ $? -ne 0 ]; then
	echo "Failed to build new jar. Aborting."
	exit 1
fi

echo "Copying new jar."
#cp jar to current directory
cp vivio.googlecode.com/svn/trunk/Vivio/vivio.jar .

if [ $? -ne 0 ]; then
	echo "Failed to copy new jar. Aborting."
	exit 1
fi

echo "Launching new jar."
#launch jar
java -jar vivio.jar -o Seiyria -c kellyirc &

echo "Removing old files."
#remove old vivio
rm -r vivio.googlecode.com/