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
        try:
            cur.execute("INSERT INTO disaster (entry_id, fema_id, type, name, start_date, end_date) VALUES (%s, %s, %s, %s, to_date(%s, 'YYYY-MM-DD'), to_date(%s, 'YYYY-MM-DD'));", (count, int(row[0].replace('\ufeff', '')), row[2], row[3], row[4], row[5]))
            conn.commit()
            cur.execute("SELECT geofib FROM county WHERE name ILIKE %s AND state ILIKE %s;", (row[6].strip(), row[1]))
            response = cur.fetchall()
            if len(response) > 0:
                cur.execute("INSERT INTO county_disaster_link (geofib, disaster_id) VALUES (%s, %s);", (int(response[0][0]), int(count)))
                conn.commit()
                print("Written")
            else:
                print("No mathing geofib")
        except:
            print("error")
        count += 1
conn.close()