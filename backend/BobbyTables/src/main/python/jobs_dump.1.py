import psycopg2
import csv
import sys

conn = psycopg2.connect(database="sample_db", user = "ethanzeigler", host = "127.0.0.1", port = "5432")

cur = conn.cursor()
filename = "/Users/ethanzeigler/Downloads/jobs.csv"

with open(filename, 'rt') as csvfile:
    rdr = csv.reader(csvfile, delimiter=',', quotechar='|')
    i=0
    count = 0
    for row in rdr:
        year = 2001
        rownum = 2
        cur.execute("SELECT COUNT(*) FROM county WHERE county.geofib = '" + row[0] + "';")
        num = cur.fetchone()[0]
        if num > 0:
            for x in range(2001, 2018):
                try:
                    cur.execute("SELECT COUNT(*) FROM county WHERE county.geofib = '" + row[0] + "';")
                    num = cur.fetchone()[0]
                    if num > 0:
                        cur.execute("INSERT INTO statistic (id, date, category, name, data) VALUES (%s, to_date(%s, 'YYYY-MM-DD'), %s, %s, %s);", (count, str(year)+"-01-01" , 'Economics', 'Total Jobs', row[rownum]))
                        conn.commit()
                        print("here")
                        sys.stdout.flush()
                        cur.execute("INSERT INTO statistic_county_link (statistic, geofib) VALUES (%s, %s);", (int(count), row[0]))
                        conn.commit()
                        print("Written")
                except Exception as e:
                    print(str(e))
                year += 1
                rownum += 1
                count += 1
conn.close()
