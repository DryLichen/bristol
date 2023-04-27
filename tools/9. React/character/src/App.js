import './App.css';
import React from 'react'

class App extends React.Component {
  constructor() {
    super();
    this.increasePoints = this.increasePoints.bind(this);
    this.decreasePoints = this.decreasePoints.bind(this);
  }

  state = {
    stats_points: 4,
    charisma_points: 0,
    prowess_points: 0,
    agility_points: 0,
    strength_points: 0,
  }

  increasePoints(stat) {
    if (this.state.stats_points === 0) {
      return;
    }
    
    if (stat === "Charisma") {
      if (this.state.charisma_points === 2) {
        return;
      }
      this.setState({charisma_points: this.state.charisma_points + 1})
    } else if (stat === "Prowess") {
      if (this.state.prowess_points === 2) {
          return;
      }
      this.setState({prowess_points: this.state.prowess_points + 1})
    } else if (stat === "Agility") {
      if (this.state.agility_points === 2) {
        return;
      }
      this.setState({agility_points: this.state.agility_points + 1})
    } else if (stat === "Strength") {
      if (this.state.strength_points === 2) {
        return;
      }
      this.setState({strength_points: this.state.strength_points + 1})
    }

    this.setState({stats_points: this.state.stats_points - 1});
  }

  decreasePoints(stat) {
    if (this.state.stats_points === 4) {
      return;
    }

    if (stat === "Charisma") {
      if (this.state.charisma_points === 0) {
        return;
      }
      this.setState({charisma_points: this.state.charisma_points - 1});
    } else if (stat === "Prowess") {
      if (this.state.prowess_points === 0) {
        return;
      }
      this.setState({prowess_points: this.state.prowess_points - 1});
    } else if (stat === "Agility") {
      if (this.state.agility_points === 0) {
        return;
      }
      this.setState({agility_points: this.state.agility_points - 1});
    } else if (stat === "Strength") {
      if (this.state.strength_points === 0) {
        return;
      }
      this.setState({strength_points: this.state.strength_points - 1});
    }

    this.setState({stats_points: this.state.stats_points + 1});
  }

  render() {
    return (
      <div>
        <RemainingStats stats_points={this.state.stats_points} />
        <table>
          <tr>
            <th>stat</th>
            <th>points</th>
            <th>increase</th>
            <th>decrease</th>
          </tr>
          <StatEditor stat="Charisma" stats_points={this.state.stats_points} stat_points={this.state.charisma_points} 
          increasePoints={this.increasePoints} decreasePoints={this.decreasePoints} />
          <StatEditor stat="Prowess" stats_points={this.state.stats_points} stat_points={this.state.prowess_points} 
          increasePoints={this.increasePoints} decreasePoints={this.decreasePoints} />
          <StatEditor stat="Agility" stats_points={this.state.stats_points} stat_points={this.state.agility_points} 
          increasePoints={this.increasePoints} decreasePoints={this.decreasePoints} />
          <StatEditor stat="Strength" stats_points={this.state.stats_points} stat_points={this.state.strength_points} 
          increasePoints={this.increasePoints} decreasePoints={this.decreasePoints} />
        </table>
      </div>
    )
  }
}

class StatEditor extends React.Component {
  constructor(props) {
    super(props);
    this.increasePoints = this.increasePoints.bind(this);
    this.decreasePoints = this.decreasePoints.bind(this);
  }

  increasePoints(stat) {
    this.props.increasePoints(stat);
  }

  decreasePoints(stat) {
    this.props.decreasePoints(stat);
  }

  canIncrease() {
    return this.props.stats_points > 0 && this.props.stat_points < 2;
  }

  canDecrease() {
    return this.props.stats_points < 4 && this.props.stat_points > 0;
  }

  
  render() {
    const disableIncreaseButton = !this.canIncrease();
    const disableDecreaseButton = !this.canDecrease();
    return (
      <div>
          <tr>
            <td>{this.props.stat}</td>
            <td>{this.props.stat_points}</td>
            <td>
              <button onClick={() => this.increasePoints(this.props.stat)} disabled={disableIncreaseButton}>
                increase
              </button>
            </td>
            <td>
              <button onClick={() => this.decreasePoints(this.props.stat)} disabled={disableDecreaseButton}>
                decrease
              </button>
            </td>
          </tr>
      </div>
    )
  }
}

class RemainingStats extends React.Component {
  render() {
    return (
      <p>Remaining stats: {this.props.stats_points}</p>
    )
  }
}

export default App;
