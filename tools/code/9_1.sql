SELECT SUM(votes) FROM Candidate;

SELECT SUM(Candidate.votes) AS votes, 
	Ward.electorate/SUM(Ward.electorate) AS percentage 
	FROM Candidate 
	LEFT JOIN Ward 
	ON Candidate.ward = Ward.id 
	WHERE Ward.name = "Windmill Hill";

CREATE TEMPORARY TABLE IF NOT EXISTS totalvotes 
SELECT SUM(votes) as total
FROM Candidate 
INNER JOIN Ward 
ON Candidate.ward = Ward.id
INNER JOIN Party 
ON Party.id = Candidate.party
WHERE Ward.name = "Southville";

SELECT Candidate.name, 
             Party.name,
             Ward.name,
             Candidate.votes,
             100.0*SUM(votes)/totalvotes.total as percentage
FROM Candidate 
INNER JOIN Ward 
ON Candidate.ward = Ward.id
INNER JOIN Party 
ON Party.id = Candidate.party
JOIN totalvotes
WHERE Ward.name = "Southville"
GROUP BY Candidate.name;

SELECT Candidate.name, Party.name, Candidate.votes/
	(SELECT SUM(Candidate.votes) FROM Candidate INNER JOIN Ward ON Ward.id = Candidate.ward WHERE Ward.name = "Southville") 
	AS percentage 
	FROM Candidate INNER JOIN Ward ON Ward.id = Candidate.ward 
	INNER JOIN Party ON Party.id = Candidate.party 
	WHERE Ward.name = "Southville" 
	ORDER BY percentage DESC;

SELECT Ward.name AS w1, 100*SUM(Candidate.votes)/
	(SELECT SUM(Candidate.votes) 
        FROM Candidate 
        INNER JOIN Ward 
        ON Ward.id = Candidate.ward
        WHERE Ward.name = w1) 
	AS percentage 
	FROM Candidate 
    INNER JOIN Ward 
    ON Ward.id = Candidate.ward 
	INNER JOIN Party 
    ON Party.id = Candidate.party 
	WHERE Party.name = "Conservative" 
	GROUP BY Ward.name;

SELECT rank
    FROM
    (SELECT Party.name AS name, ROW_NUMBER() OVER (
        ORDER BY SUM(Candidate.votes) DESC) AS rank
        FROM Party
        INNER JOIN Candidate
        ON Party.id = Candidate.party
        INNER JOIN Ward
        ON Ward.id = Candidate.ward
        WHERE Ward.name = "Whitchurch Park"
        GROUP BY Party.name) AS temp_table
    WHERE temp_table.name = "Labour";

SELECT Party.name AS party, 
    SUM(Candidate.votes) AS votes
    FROM Party
    INNER JOIN Candidate
    ON Party.id = Candidate.party
    GROUP BY Party.name;

SELECT Ward.name AS ward, 
    tab_green.votes - tab_labour.votes AS difference
    FROM
    (SELECT Candidate.ward AS ward,
    SUM(Candidate.votes) AS votes
    FROM Party
    INNER JOIN Candidate
    ON Party.id = Candidate.party
    WHERE Party.name = "Green"
    GROUP BY ward) AS tab_green
    INNER JOIN
    (SELECT Candidate.ward AS ward,
    SUM(Candidate.votes) AS votes
    FROM Party
    INNER JOIN Candidate
    ON Party.id = Candidate.party
    WHERE Party.name = "Labour"
    GROUP BY ward) AS tab_labour
    ON tab_green.ward = tab_labour.ward
    INNER JOIN Ward
    ON Ward.id = tab_green.ward
    WHERE tab_green.votes > tab_labour.votes
    ORDER BY difference DESC;

