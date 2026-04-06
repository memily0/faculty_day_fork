1) 74 

    SELECT id,
    CASE
    WHEN has_internet THEN 'YES'
    ELSE 'NO'
    END AS has_internet
    FROM Rooms

2) 56

    DELETE
    FROM Trip
    WHERE town_from = 'Moscow' 

3) 114

   SELECT p.name  
   FROM Pilots p
   JOIN Flights f ON p.pilot_id = f.second_pilot_id
   WHERE f.destination = 'New York'
   AND f.flight_date >= '2023-08-01'
   AND f.flight_date < '2023-09-01';

4) 19 

   SELECT DISTINCT fm.status
   FROM FamilyMembers fm
   JOIN Payments p
   ON p.family_member = fm.member_id
   JOIN Goods g
   ON g.good_id = p.good
   WHERE g.good_name = 'potato';

5) 21 

   SELECT g.good_name
   FROM  Goods g
   JOIN  Payments p
   ON g.good_id = p.good
   GROUP BY g.good_name
   HAVING COUNT(*) > 1

6) 32 

   SELECT FLOOR(AVG(EXTRACT(YEAR FROM AGE('2026-06-04', birthday)))) AS age
   FROM FamilyMembers;
