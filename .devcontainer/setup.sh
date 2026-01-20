#!/bin/bash

echo "===== Installing Android SDK ====="

sudo apt-get update
sudo apt-get install -y unzip wget

# Create SDK directory
mkdir -p /home/codespace/android-sdk/cmdline-tools
cd /home/codespace/android-sdk

# Download command line tools
wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip -O sdk.zip
unzip -o sdk.zip -d cmdline-tools
mv cmdline-tools/cmdline-tools cmdline-tools/latest

# Set env permanently
echo 'export ANDROID_HOME=/home/codespace/android-sdk' >> ~/.bashrc
echo 'export PATH=$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH' >> ~/.bashrc
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc

export ANDROID_HOME=/home/codespace/android-sdk
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH

# Accept licenses & install packages
yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses

$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager \
"platform-tools" \
"platforms;android-34" \
"build-tools;34.0.0"

# Create local.properties automatically
echo "sdk.dir=/home/codespace/android-sdk" > /workspaces/second-brain-buddy/local.properties

echo "===== Setup Completed ====="
java -version
