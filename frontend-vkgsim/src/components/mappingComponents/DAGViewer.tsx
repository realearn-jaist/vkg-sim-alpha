import React from "react";
import { Graphviz } from "graphviz-react";

interface GraphvizComponentProps {
    dot: string;
    width?: string | number;
    height?: string | number;
}

const GraphvizComponent: React.FC<GraphvizComponentProps> = ({ dot, width, height }) => {
    // Generate CSS from props
    const style: React.CSSProperties = {
        width: width || "100%",
        height: height || "100%",
    };

    return (
        <div style={{ ...style, position: "relative" }}>
            {dot !== "" ? (
                <>
                    <Graphviz
                        dot={dot}
                        options={{
                            useWorker: false,
                            zoom: true,
                            ...style,
                        }}
                    />
                </>
            ) : null}
        </div>
    );
};

export default GraphvizComponent;
