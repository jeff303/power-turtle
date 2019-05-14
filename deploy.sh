#!/bin/bash
set -e
cd $(dirname $0)
#lein min
cd resources/public
git init
git add . --force
git commit -m "Deploy to GitHub Pages"
git push --force --quiet "git@github.com:timothypratley/power-turtle.git" master:gh-pages
rm -fr .git
