FROM ubuntu



# Update the repository sources list
RUN apt-get update

# Install and run apache
RUN apt-get install -y apache2 && apt-get clean

VOLUME /var/www/html


EXPOSE 80
CMD ["apachectl", "-D", "FOREGROUND"]
