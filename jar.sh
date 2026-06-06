VERSAO=$1
javac -cp "gdx-jogo/libs/*" -d . gdx-jogo/src/com/micro/**/*.java
jar cvf micro-$VERSAO.jar com/micro
rm -rf com