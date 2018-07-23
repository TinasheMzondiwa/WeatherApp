if [[ $BACKGROUND && ${BACKGROUND} && $BACKGROUND_URI && ${BACKGROUND_URI} ]]
then
    echo "Background images file detected - downloading..."
    curl -L -o ${BACKGROUND} ${BACKGROUND_URI}
else
    echo "Background images file uri not set.  .Build will fail."
fi
