import React, {useEffect} from 'react';
import {Form} from "react-bootstrap";
import {ParentCommunication, sendMessage} from "./ParentCommunication";

export function ConfigurationScreen({account}: any) {
  useEffect(() => {
    sendMessage({
      minHeight: 400
    });
    sendMessage({
      query: `
      # Write your query or mutation here
      query {
        configs(where:{
          filter: {
            eq: {
              path: "account"
              value: "acme-issues"
            }
          }
        }) {
          nodes {
            id
            appId
            account
            config
          }
        }
      }
      `
    })
  });
  return (
    <ParentCommunication onMessage={(msg: any) => {
        console.log('parent communication', msg);
      }}>
      <Form style={{margin: '2em'}}>
          <h2>Configuration for {account}</h2>
          <Form.Group controlId="formBasicEmail">
              <Form.Label>Acme Github Issues Thing</Form.Label>
              <Form.Control type="email" name={'' + Math.random()} placeholder="Enter something" onChange={(e: any) => sendMessage({config: e.target.value})} />
              <Form.Text className="text-muted">
                  We'll never steal your thunder.
              </Form.Text>
          </Form.Group>
      </Form>
    </ParentCommunication>
  );
}
