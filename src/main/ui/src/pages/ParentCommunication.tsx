import React, { useEffect } from 'react';

export interface IParentCommunicationProps {
  children: React.ReactNode;
  onMessage: (msg: any) => void;
}

export interface ParentMessageType {
  query?: string;
  minHeight?: number | string;
  config?: any;
}

export function sendMessage(message: ParentMessageType) {
  if(window.parent && window.parent.postMessage) {
    // TODO send id to get specific response
    window.parent.postMessage(JSON.stringify(message), '*');
  }
}

export function ParentCommunication({children, onMessage}: IParentCommunicationProps) {
  useEffect(() => {
    const componentId = Math.random();
    // Parent
    // Send messages to iframe using iframeEl.contentWindow.postMessage Recieve messages using window.addEventListener('message')
    //
    // iframe
    // Send messages to parent window using window.parent.postMessage Recieve messages using window.addEventListener('message')
    const msgListener: (this: Window, ev: WindowEventMap['message']) => any = (msg) => {
      const obj = JSON.parse(msg.data);
      onMessage(obj);
    };

    window.addEventListener('message', msgListener);
    return () => {
      window.removeEventListener('message', msgListener);
    }
  });

  return <>{children}</>;
}
