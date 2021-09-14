# Comp261
Overall Grade : A

A1 - Coverage
• Parsing, data structures, and drawing. (Construct classes to represent Stops, Trips, and Connections, Make methods to read the data files, parse them, and create your data structures, Draw the graph by filling in the redraw method left abstract in the GUI class. This method should call a draw method on the Connection and Stop, which should use the passed Graphics object. )
• Using the graph structure and other functionality. (Allow the user to navigate the map, i.e. implement panning and zooming with the buttons.
• Make the program respond to the mouse so that the user can select a stop with the mouse, and the program will then highlight it, and print out the name of the stop and the id of all the trips going through the stop. (linear search)
• Implement the behaviour of the search box in the top right, which should allow a user to select a Stop by entering its name (trie data)
• Learn about and implement a quad-tree index of all the nodes and use it to search for the closest node to the mouse location.
• Add a drop-down suggestions box to the search box, which the user can select completions from.

A2 - Coverage
• Route finding (A* search)
• Critical intersections (articulation point)
• Improved route finding (Incorporate one-way,  merge a sequence of road segments all from the same road into a single step, and include the total length of the step)
• More improved route finding (Take into account the restriction information (Weighted A*), Add buttons to select distance or time. Include road class and speed limit information to
make your search prefer routes on high class roads and faster roads.

A3 - Coverage
• Discrete Fourier Transform (DFT),
• Inverse Discrete Fourier Transform (IDFT),
• Fast Fourier Transform (FFT) and
• Inverse Fast Fourier Transform (IFFT),

A4- Coverage
•Your task is to write a parser and interpreter which can read and parse a robot program from a file, and then execute it.
•you should extend your parser to handle the robot sensors, and IF and WHILE statements
•you should extend your parser to handle actions with optional arguments: move and wait can take an argument specifying how many move or wait steps to take, if statements with optional else clauses, arithmetic expressions that compute values with sensors and numbers, more complex conditions with logical operators and expressions; comparisons between any expressions, not just a sensor and a number, more restrictive form of integer constant, which does not allow leading zeroes.
•For this stage, you should extend your parser to handle variables and assignment statements, a sequence of elif elements in an if statement, optional arguments to barrelLR and barrelFB to access the relative position of barrels other than the closest one.
•For this stage, you should extend your parser and interpreter to, allow infix operators and optional parentheses for both arithmetic and logical expressions, Require variables to be declared before they can be used, Allow nested scope, so that variables declared inside a block (i) are only accessible within the block, and (ii) "shadow" any variables of the same name declared in the program or outer blocks.

A5 - Coverage
•String Search (your task is to implement the KMP string search algorithms to enable searching)
•Huffman coding (Your task in this part is to implement the Huffman coding and decoding algorithm, as described in lectures)
•Lempel-Ziv compression (In this part, your task is to implement the Lempel-Ziv 77 compression and decompression algorithms, as described in lecture)
•Boyer-Moore (Implement the Boyer-Moore string search algorithm, which is faster but more complex than KMP. It improves efficiency by doing a more complex search (building two tables, not just one) and starting from the end of a pattern instead of the start.)
