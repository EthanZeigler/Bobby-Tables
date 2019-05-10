import psycopg2
import csv
import sys

conn = psycopg2.connect(database="sample_db", user = "ethanzeigler", host = "127.0.0.1", port = "5432")

cur = conn.cursor()
filename = "/Users/ethanzeigler/Downloads/Disasters_final_date_nocomma.csv"

with open(filename, 'rt') as csvfile:
    rdr = csv.reader(csvfile, delimiter=',', quotechar='|')
    count = 0
    for row in rdr:
        year = 2001
        rownum = 2
        for x in range(2001, 2019):
            try:
                cur.execute("INSERT INTO statistic (entry_id, date, category, name, data) VALUES (%s, to_date(%s, 'YYYY'), %s, %s, %s);", (count, year , 'Economics', 'Total Jobs', row[rownum],))
                conn.commit()
                cur.execute("INSERT INTO statistic_county_link (statistic, geofib) VALUES (%s, %s);", (row[0], int(count)))
                conn.commit()
                print("Written")
            except:
                print("error")
            year += 1
            rownum += 1
            
        
        count += 1
conn.close()