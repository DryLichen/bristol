SELECT name FROM Party ORDER BY name ASC;

SELECT Party.name FROM Party 
    INNER JOIN Candidate 
    ON Party.id = Candidate.party
    INNER JOIN Ward
    ON Ward.id = Candidate.ward
    WHERE Ward.name = "Bedminster";

SELECT SUM(Candidate.votes)
    FROM Party
    INNER JOIN Candidate 
    ON Party.id = Candidate.party
    INNER JOIN Ward
    ON Ward.id = Candidate.ward
    WHERE Ward.name = "Stockwood"
    AND
    Party.name = "labour";

SELECT Candidate.name, Party.name, Candidate.votes
    FROM Party
    INNER JOIN Candidate 
    ON Party.id = Candidate.party
    INNER JOIN Ward
    ON Ward.id = Candidate.ward
    WHERE Ward.name = "Southville"
    ORDER BY Candidate.votes DESC;

SELECT Candidate.name, Party.name, Candidate.votes
    FROM Party
    INNER JOIN Candidate 
    ON Party.id = Candidate.party
    INNER JOIN Ward
    ON Ward.id = Candidate.ward
    WHERE Ward.name = "Knowle"
    ORDER BY Candidate.votes DESC
    LIMIT 1;
