FROM opencpu/rstudio

#RUN R -e "install.packages(c('devtools', 'jsonlite'), dependencies=TRUE,repos='http://cran.rstudio.com/')"

RUN sudo apt-get install pandoc -y
RUN sudo chmod -R 777 /usr/local/lib/R/site-library

RUN R -e "install.packages(c('devtools','xts','fpp2','scatterD3','dplyr','usdm','psych','shiny','tableHTML','sjPlot','MASS','corrplot','stringr','jsonlite','openssl','remotes'),repos='http://cran.rstudio.com/')"   

RUN R -e "devtools::install_github('mattflor/chorddiag')"
RUN R -e "devtools::install_github('ubitech/Physiognomica',force=TRUE)"
