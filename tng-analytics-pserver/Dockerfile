FROM python

LABEL image tng-analytics-pserver

WORKDIR /docker-flask

#COPY ./tng-analytics-pserver .
COPY  . .

RUN chmod -R 777 /docker-flask

RUN pip3 install --upgrade pip && pip install -r requirements.txt

CMD python3 ./app.py

