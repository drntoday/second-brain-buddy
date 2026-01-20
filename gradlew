#!/usr/bin/env sh
exec java -cp "gradle/lib/*" org.gradle.launcher.GradleMain "$@"