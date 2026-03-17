import React, { useEffect, useState } from "react";
import './DictionaryPage.css';

const DictionaryPage = () => {
    const [words, setWords] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetch("/api/dictionary/all")
            .then((res) => {
                if (!res.ok) throw new Error("Network response was not ok");
                return res.json();
            })
            .then((data) => {
                setWords(data);
                setLoading(false);
            })
            .catch((err) => {
                console.error("Error fetching dictionary:", err);
                setLoading(false);
            });
    }, []);

    if (loading) return <p>Loading dictionary...</p>;

    return (
        <div className="dictionary-container">
            <h1>Dictionary</h1>
            <table>
                <thead>
                <tr>
                    <th>Word</th>
                    <th>Point Value</th>
                </tr>
                </thead>
                <tbody>
                {words.map((entry) => (
                    /* Added the 'key' prop here */
                    <tr key={entry.word}>
                        <td>{entry.word}</td>
                        <td>{entry.pointValue}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default DictionaryPage;