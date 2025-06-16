import { createContext, useState } from 'react';

export const SettingsContext = createContext();

export const SettingsProvider = ({ children }) => {
    const [helpMode, setHelpMode] = useState(false);
    const [autoOpenPlayerHand, setAutoOpenPlayerHand] = useState(false);

    return (
        <SettingsContext.Provider value={{ helpMode, setHelpMode, autoOpenPlayerHand, setAutoOpenPlayerHand }}>
            {children}
        </SettingsContext.Provider>
    );
};
