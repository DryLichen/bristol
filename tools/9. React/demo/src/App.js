import './App.css';
import React from 'react';

class App extends React.Component {
  constructor() {
    super();
    this.onNameChange = this.onNameChange.bind(this);
  }

  onNameChange(newName) {
    this.setState({name: newName});
  }

  state = {
    name: 'David'
  }

  render() {
    return (
      <div>
        <NameEditor name={this.state.name} onNameChange={this.onNameChange} />
        <NameGreeter name={this.state.name} />
      </div>
    )
  }
}

class NameGreeter extends React.Component {
  render() {
    if (this.props.name === "") {
      return (
        <p>Hello!</p>
      )
    } else {
      return (
        <p>Hello, {this.props.name}!</p>
      )
    }
  }
}

class NameEditor extends React.Component {
  constructor(props) {
    super(props);
    this.onNameChange = this.onNameChange.bind(this);
  }

  onNameChange(e) {
    this.props.onNameChange(e.target.value);
  }

  render() {
    return (
      <p>
        <label for="name">Name: </label>
        <input type='text' id='name' value={this.props.name} onChange={this.onNameChange} />
      </p>
    )
  }
}

export default App;
