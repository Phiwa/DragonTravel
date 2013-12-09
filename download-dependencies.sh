wget http://dev.bukkit.org/media/files/753/8/Factions.jar -O factions-2.2.2.jar
mvn install:install-file -Dfile=factions-2.2.2.jar -Dversion=2.2.2 -DartifactId=factions -DgroupId=com.massivecraft -Dpackaging=jar

wget http://dev.bukkit.org/media/files/753/422/mcore.jar -O mcore-6.9.1.jar
mvn install:install-file -Dfile=mcore-6.9.1.jar -Dversion=6.9.1 -DartifactId=mcore -DgroupId=com.massivecraft -Dpackaging=jar
