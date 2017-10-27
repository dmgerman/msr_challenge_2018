#!/bin/bash

echo "Operating in" $(pwd)/*
# Loop over inner directories
for path in $(pwd)/*; do
    [ -d "${path}" ] || continue
    pushd ${path}
    # Extract any zip files
    for archive in *.zip; do
        rm *.json
        mkdir tmp
        unzip ${archive} -d tmp
        pushd tmp
        for file in *.json; do
            mv "${file}" ../"${archive}-${file}"
        done
        popd
        rm -rf tmp
    done
    popd
done

