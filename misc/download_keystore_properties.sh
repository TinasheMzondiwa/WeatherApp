# use curl to download a keystore from $KEYSTORE_URI, if set,
# to the path/filename set in $KEYSTORE_PROP.
if [[ $KEYSTORE_PROP && ${KEYSTORE_PROP} && $KEYSTORE_PROP_URI && ${KEYSTORE_PROP_URI} ]]
then
    echo "Keystore properties detected - downloading..."
    curl -L -o ${KEYSTORE_PROP} ${KEYSTORE_PROP_URI}
else
    echo "Keystore properties uri not set.  .APK artifact will not be signed."
fi
