import React, { useEffect, useState } from "react";
import './DictionaryPage.css';


/**
 * DictionaryPage
 *
 * Fetches all dictionary words from the backend and displays them
 * in a simple table with "Word" and "Point Value" columns.
 */
const DictionaryPage = () => {
    const [words, setWords] = useState([]);

    useEffect(() => {
        // Fetch all words from your backend with an empty header
        fetch("http://localhost:8080/api/dictionary/all", {
            method: "GET"
        })
            .then((res) => res.json())
            .then((data) => setWords(data))
            .catch((err) => console.error("Error fetching dictionary:", err));
    }, []);

    return (
        <div>
            <h1>Dictionary</h1>
            <table>
                <thead>
                <tr>
                    <th>Word</th>
                    <th>Point Value</th>
                </tr>
                </thead>
                <tbody>
                {words.map((word) => (
                    <tr key={word.id}>
                        <td>{word.word}</td>
                        <td>{word.pointValue}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};
export default DictionaryPage;