
echo "wgetting new version."
wget  -qr http://vivio.googlecode.com/svn/trunk

echo "Building new binary files."
call ant -buildfile vivio.googlecode.com/svn/trunk/Vivio/build-bin.xml

echo "Building new jar."
call ant -buildfile vivio.googlecode.com/svn/trunk/Vivio/build-jar.xml

echo "Copying new jar."
copy vivio.googlecode.com\svn\trunk\Vivio\vivio.jar .

echo "Removing old files."
rmdir /S /Q vivio.googlecode.com\

echo "Launching new jar."
timeout 5
java -jar vivio.jar -s irc.esper.net -o Freek -c kellyirc