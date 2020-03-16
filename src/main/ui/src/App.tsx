import React from 'react';
import '@cloudbees/honeyui/dist/index.css';
import {BrowserRouter as Router, Route, RouteComponentProps} from 'react-router-dom';
import {ConfigurationScreen} from "./pages/ConfigurationScreen";

const App: React.FC = () => {
    return (
        <div className="App">
            <Router>
                <Route path="/configure/:account" render={(props: RouteComponentProps<any>) => <ConfigurationScreen account={props.match.params.account} />} />
            </Router>
        </div>
    );
};

export default App;
