#!/bin/bash

# Select all environment variables containing a dot (those can be used as -D parameters):
CMD="$1"

CONFIGFILE="$HOME/instance.conf"

echo "# Generated configuration" > $CONFIGFILE
echo "include \"application.conf\"" >> $CONFIGFILE

while read -r parameter; do
	echo "Adding Java options: $parameter"
	case $parameter in
		*\"null\") echo "$parameter" | sed -r 's/\"null\"/null/' >> $CONFIGFILE;;
		*\"true\") echo "$parameter" | sed -r 's/\"true\"/true/' >> $CONFIGFILE;;
		*\"false\") echo "$parameter" | sed -r 's/\"false\"/false/' >> $CONFIGFILE;;
		*) echo "$parameter" >> $CONFIGFILE;;
	esac
done <<< "$(env | grep -E '^[a-zA-Z0-9_]+\.[a-zA-Z0-9_\.]+=' | sed -r 's/^([a-zA-Z0-9_\.]*)=(.*)$/\1=\"\2\"/')"

JAVA_OPTS="$JAVA_OPTS -Dconfig.file=$CONFIGFILE"

export JAVA_OPTS

echo "Running command: $CMD"
echo "ParameterS: $JAVA_OPTS"

exec $CMD