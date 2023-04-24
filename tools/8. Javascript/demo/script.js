window.onload = function () {
  let wards = fetch('https://opendata.bristol.gov.uk/api/v2/catalog/datasets/wards/records?limit=50&select=name,ward_id')
    .then(response => response.json())
    .then(populateWards)
    .catch(err => console.log(err));
}

function populateWards(wards) {
  let buttons = new DocumentFragment();

  wards.records.
  sort((a, b) => a.record.fields.name.localeCompare(b.record.fields.name)).
  forEach(w => {
      const [id, name] = [w.record.fields.ward_id, w.record.fields.name];
      const b = document.createElement("button");
      b.textContent = name;
      // b.onclick = displayData(id, name);
      b.addEventListener("click", displayData(id, name));
      b.style.display = "block";
      buttons.appendChild(b);
  });
  
  let nav = document.getElementById("nav");
  nav.textContent = '';
  nav.append(buttons);
}

function displayData(id, name) {
  
  function buildPopulation(records) {

    // Make heading
    let heading = document.createElement('h2');
    heading.textContent = 'Population';

    // Make table
    let table = document.createElement('table');
    table.setAttribute('id','populationTable');

    // Make table header
    let header = document.createElement('tr');
    header.innerHTML = '<th>Year</th><th>Population</th>';
    table.append(header);
    
    // Populate table
    records.filter(d => d.record.fields.mid_year >= 2015)
      .sort((x1, x2) => x1.record.fields.mid_year < x2.record.fields.mid_year ? -1 : 1)
      .map(r =>
        table.append(createElementWith('tr', [
          createElementText('td', r.record.fields.mid_year),
          createElementText('td', r.record.fields.population_estimate)
        ]))
      )
    // records.filter(x => x.record.fields.mid_year >= 2015)
    //   .sort((x1, x2) => x1.record.fields.mid_year < x2.record.fields.mid_year ? -1 : 1)
    //   .forEach(r => {
    //     let year = document.createElement('td');
    //     year.textContent = r.record.fields.mid_year;
    //     let population = document.createElement('td');
    //     population.textContent = r.record.fields.population_estimate;

    //     let row = document.createElement('tr');
    //     row.append(year, population);
    //     table.append(row);
    // });
    
    let population = new DocumentFragment();
    population.append(heading, table);
    
    return population;
  }

  function buildLifeExpectancy(records) {
    // make headings
    let header = document.createElement("h2");
    header.textContent = "Life Expectancy";
    header.style.textAlign = "center";

    // make tables
    let table = document.createElement("table");
    table.setAttribute('id', 'lifeExpectancyTable');

    // make table header
    let tableHead = document.createElement('tr');
    tableHead.innerHTML = '<th>Year</th><th>Female_Life_Expectancy</th><th>Male_Life_Expectancy</th>';
    table.append(tableHead);

    // populate table
    records.sort((r1, r2) => r1.record.fields.year.localeCompare(r2.record.fields.year))
      .map(r =>
        table.append(createElementWith('tr', [
          createElementText('td', r.record.fields.year),
          createElementText('td', r.record.fields.female_life_expectancy),
          createElementText('td', r.record.fields.male_life_expectancy)
        ]))
      )
    // records.forEach(r => {
    //   let row = document.createElement('tr');

    //   let year = document.createElement('td');
    //   year.textContent = r.record.fields.year;
    //   let female = document.createElement('td');
    //   female.textContent = r.record.fields.female_life_expectancy;
    //   let male = document.createElement('td');
    //   male.textContent = r.record.fields.male_life_expectancy;

    //   row.append(year, female, male);
    //   table.appendChild(row);
    // })
    
    let lifeExpectancy = new DocumentFragment();
    lifeExpectancy.append(header, table);

    return lifeExpectancy;
  }

  return function () {
    let first_url = `https://opendata.bristol.gov.uk/api/v2/catalog/datasets/population-estimates-time-series-ward/records?limit=20&select=mid_year,population_estimate&refine=ward_2016_code:${id}`;
    let second_url = `https://opendata.bristol.gov.uk/api/v2/catalog/datasets/life-expectancy-in-bristol/records?limit=20&year,select=female_life_expectancy,male_life_expectancy&refine=ward_code:${id}`;

    let wards = fetch(first_url)
      .then(response => response.json())
      .then(data1 => {
        fetch(second_url)
        .then(response => response.json())
        .then(data2 => {
          let heading = document.createElement('h1');
          heading.textContent = name;

          let population = buildPopulation(data1.records);
          let lifeExpectancy = buildLifeExpectancy(data2.records);

          let dataPane = document.getElementById("dataPane");
          dataPane.textContent = '';
          dataPane.append(heading, population, lifeExpectancy);
        })
      })
      .catch(err => console.log(err));
  }
    // Promise.all([fetch(first_url), fetch(second_url)])
    //   .then([response1, response2] => Promise.all([response1.json(), response2.json()]))
    //   .then([data1, data2]) => {
    //     let heading = document.createElement('h1');
    //     heading.textContent = name;

    //     let population = buildPopulation(data1.records);
    //     let lifeExpectancy = buildLifeExpectancy(data2.records);

    //     let dataPane = document.getElementById("dataPane");
    //     dataPane.textContent = '';
    //     dataPane.append(heading, population, lifeExpectancy);
    //   })
    //   .catch(err => console.log(err));
}

function createElementText(tag, text) {
  let element = document.createElement(tag);
  element.textContent = text;
  return element;
}

function createElementWith(tag, xs) {
  let element = document.createElement(tag);
  for (const x of xs) {
    element.append(x);
  }
  return element;
}
