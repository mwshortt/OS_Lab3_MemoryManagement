Madeline Shortt

A. In your implementation of segmentation which of the best fit, first fit, or worst fit memory allocation policy do you use to find a free memory region for each segment? Why did you pick this policy? (5)

In my implementation of segmentation I used the memory allocation policy of best fit to find a free memory region for each segment. I chose this policy because it tries to reduce wasted space by finding the smallest hole that is just large enough for the segment and using it. 
I also wanted to push myself to implement a more 'thoughtful' algorithm. To implement worst fit, you would just need an ordered set and then choose the last element, which would be the largest one. To implement first fit, you wouldn't even need an ordered set, you would just iterate through the set using the first hole that was large enough to accomodate the segment. Implementing best fit required me to use an efficient searching algorithm and carefully choose my data structures. 

B. What data structures and search algorithm do you use for searching through the list of holes (in segmentation)? You will get 5 points for implementing a brute-force linear search. If you implement anything more efficient than this, you will get full 10 points. (10)

I stored my list of holes in a TreeSet. I chose a TreeSet because I wanted an ordered set that I could control the specifications of the ordering. I implemented three different, custom comparator schemes (SizeComparator, StartComparator, and EndComparator) so I could sort my TreeSet list of holes by either the size of the hole, the start index of the hole or the end index of the hole. To find the best fit hole I used TreeSet's ceiling() method that returns the least element in this set greater than or equal to the given element. This method takes advantage of the sorted nature of the set and is O(logN) so it is much more efficient that a brute-force linear search.  

C. For segmentation, what data structure do you use to track the start location of each segment? (2)

I chose to use a HashMap to keep track of all the information relating to a process. The key of the process HashMap is the process id and the value is an array of integers. This array has very specific information relating the each one of its indices. [0] = start of the text segment, [1] = size of the text segment, [2] = start of the data segment, [3] = size of the data segment, [4] = start of the heap segment, [5] = size of the heap segment, [6] = amount of internal fragmentation in the process, [7] = size of the process. 

F. How do the levels of internal and external fragmentation compare when you run the sample input in “sample.txt” with each of your allocators? Why is this the case? (5 + 5)

Segmentation has some external fragmentation, while Paging has none. This is because Paging's blocks are of a fixed size while the size of the segments are completely variable and unpredictable. 

G. Write an input test case where the Segmentation allocator has little internal fragmentation. Explain why this test case produces the result you see. (3)

Here is my input test case:
1024 0
A 300 1 100 100 100
A 300 2 150 80 70
A 220 3 90 60 70
P

The print gives me:
SEGMENTATION: 
Memory size = 1024 bytes, allocated bytes = 820, free = 204
There are currently 1 holes and 3 active processes
Hole list: 
hole 1: start location = 820, size = 204
Process list:
process id = 1, size = 300, allocation = 300
text start = 0, size = 100
data start = 100, size = 100
text start = 200, size = 100
process id = 2, size = 300, allocation = 300
text start = 300, size = 150
data start = 450, size = 80
text start = 530, size = 70
process id = 3, size = 220, allocation = 220
text start = 600, size = 90
data start = 690, size = 60
text start = 750, size = 70
Total internal fragmentation = 0 bytes
Failed allocations due to no memory = 0
Failed allocations due to external fragmentation = 0

In my implementation, internal fragmentation occurs only when a segment is assigned to a hole that is only 16 bytes larger than it or less. 
In this example there are only allocations, and by the end of it there is still plenty of room left in memory (204 bytes). This means that the segments are always faced with one large hole, a hole much larger than the size of the segment. So the segment takes a small portion of that hole and there's plenty left to create a new hole. So the segment never has to include a very small hole (which happens if less than 16 bytes is left over from the hole) and create internal fragmentation. 
