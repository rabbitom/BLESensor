/*
 *	Copyright (c) 2011-2015 NinevehGL. More information at: http://nineveh.gl
 *
 *	Permission is hereby granted, free of charge, to any person obtaining a copy
 *	of this software and associated documentation files (the "Software"), to deal
 *	in the Software without restriction, including without limitation the rights
 *	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *	copies of the Software, and to permit persons to whom the Software is
 *	furnished to do so, subject to the following conditions:
 *
 *	The above copyright notice and this permission notice shall be included in
 *	all copies or substantial portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *	THE SOFTWARE.
 */

#import "NGLRuntime.h"
#import "NGLGlobal.h"

/*!
 *					Retrieves the file's name + file's extension.
 *
 *					If the path contains only the file, with its extension or not, itself will be returned.
 *
 *	@param			named
 *					A NSString (name or path) containing the file's name + file's extension.
 *
 *	@result			An autoreleased NSString containing the result.
 */
NGL_API NSString *nglGetFile(NSString *named);

/*!
 *					Retrieves the file's extension only.
 *
 *					If there is no extension, a blank value will be returned (@"").
 *
 *	@param			named
 *					A NSString (name or path) containing the file's extension.
 *
 *	@result			An autoreleased NSString containing the result.
 */
NGL_API NSString *nglGetFileExtension(NSString *named);

/*!
 *					Retrieves the file's name only.
 *
 *					This function ignores the file's extension if it exists.
 *
 *	@param			named
 *					A NSString (name or path) containing the file's name.
 *
 *	@result			An autoreleased NSString containing the result.
 */
NGL_API NSString *nglGetFileName(NSString *named);

/*!
 *					Retrieves the path only.
 *
 *					This function ignores any file in the path and returns only the path. If there's
 *					only the file, without a path, in the input parameter, a blank string will be
 *					returned (@"").
 *
 *	@param			named
 *					A NSString (name or path) containing a system path to be isolated.
 *
 *	@result			An autoreleased NSString containing the result.
 */
NGL_API NSString *nglGetPath(NSString *named);

/*!
 *					Makes the real path based on the input path.
 *
 *					If the file is found with the input path, itself will be returned, without
 *					changes. But if the file was not found in the input path, the NinevehGL Global Path
 *					will be returned in place.
 *
 *	@param			named
 *					A NSString (name or path) containing the file's name.
 *
 *	@result			An autoreleased NSString containing the result.
 */
NGL_API NSString *nglMakePath(NSString *named);

/*!
 *					Loads the source from a file using the NinevehGL Global Path API.
 *
 *					This function doesn't use NSData, so it can't load binary files.
 *
 *	@param			named
 *					A NSString (name or path) containing the file's name.
 *
 *	@result			An autoreleased NSString containing the result.
 */
NGL_API NSString *nglSourceFromFile(NSString *named);

/*!
 *					Loads the source from a file using the NinevehGL Global Path API.
 *
 *					This function returns an autoreleased instance of NSData. This function is indicated
 *					to load binary files.
 *
 *	@param			named
 *					A NSString (name or path) containing the file's name.
 *
 *	@result			An autoreleased NSData containing the result.
 */
NGL_API NSData *nglDataFromFile(NSString *named);

/*!
 *					Extracts a content from a NSString without including the pattern.
 *
 *					This function extracts the string between two patterns without including them in the
 *					result. For example:
 *
 *					<pre>
 *
 *					nglRangeWithout(@"is", @"string", @"This is a test string");
 *
 *					// The line above will result in @" a test "
 *
 *					</pre>
 *
 *					If there is more than one pattern, just the first match will be used.
 *
 *	@param			init
 *					A NSString containing the initial pattern.
 *
 *	@param			end
 *					A NSString containing the end pattern.
 *
 *	@param			source
 *					A NSString containing the source string.
 *
 *	@result			A NSRange containing the result.
 */
NGL_API NSRange nglRangeWithout(NSString *init, NSString *end, NSString *source);

/*!
 *					Extracts content from a NSString including the final pattern.
 *
 *					This function extracts the string between two patterns including the last pattern in
 *					the result. For example:
 *					<pre>
 *
 *					nglRangeWithout(@"is", @"string", @"This is a test string");
 *
 *					// The line above will result in @" a test string"
 *
 *					</pre>
 *
 *					If there is more than one pattern, just the first match will be used.
 *
 *	@param			init
 *					A NSString containing the initial pattern.
 *
 *	@param			end
 *					A NSString containing the end pattern.
 *
 *	@param			source
 *					A NSString containing the source string.
 *
 *	@result			A NSRange containing the result.
 */
NGL_API NSRange nglRangeWith(NSString *init, NSString *end, NSString *source);

/*!
 *					Converts a NSString to NSArray
 *
 *					This function extracts an array from a string, separating the elements only by
 *					white spaces or new lines. The values in the NSArray still being NSString instances.
 *
 *	@param			string
 *					A NSString containing the input string.
 *
 *	@result			A NSRange containing the result.
 */
NGL_API NSArray *nglGetArray(NSString *string);

/*!
 *					Replaces a pattern inside a C string by another.
 *
 *					This function searches for a pattern inside a C string and replaces all the matching
 *					results by another C string.
 *
 *	@param			string
 *					A C string containing the input string.
 *
 *	@param			searchFor
 *					A C string containing the input string.
 *
 *	@param			replaceFor
 *					A C string containing the input string.
 */
NGL_API void nglCStringReplaceChar(char *string, char searchFor, char replaceFor);

/*!
 *					Checks if the current device orientation is a valid one. Invalid orientations are:
 *					Unkown, FaceUp and FaceDown. All the other orientations are valid.
 *
 *	@result			A BOOL indicating if the current device orientation is valid.
 */
NGL_API BOOL nglDeviceOrientationIsValid(void);

/*!
 *					Checks if the current device orientation is a valid one. Invalid orientations are:
 *					Unkown, FaceUp and FaceDown. All the other orientations are valid.
 *
 *	@result			A BOOL indicating if the current device orientation is valid.
 */
NGL_API BOOL nglDeviceOrientationIsPortrait(void);

/*!
 *					Checks if the current device orientation is a valid one. Invalid orientations are:
 *					Unkown, FaceUp and FaceDown. All the other orientations are valid.
 *
 *	@result			A BOOL indicating if the current device orientation is valid.
 */
NGL_API BOOL nglDeviceOrientationIsLandscape(void);

/*!
 *					Returns the running version of OS system.
 *
 *	@result			An unsigned int data type representing the system version.
 */
NGL_API float nglDeviceSystemVersion(void);