import { useState } from 'react';

// Justification: The ImageUploader's responsibility is now simplified. It only handles
// the file input and provides a preview. The actual upload logic is moved to the parent component,
// which is a better separation of concerns.
const ImageUploader = ({ onFileSelect }) => {
  const [imagePreviewUrl, setImagePreviewUrl] = useState('');

  // Justification: This handler now only sets the preview URL and calls the onFileSelect
  // callback with the file object, which is a cleaner design.
  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      onFileSelect(file); // <-- Send the file object to the parent
      setImagePreviewUrl(URL.createObjectURL(file));
    }
  };

  return (
    <div className="form-group mb-3">
      <label htmlFor="profileImage">Profile Image</label>
      <div className="d-flex flex-column align-items-center">
        <input
          type="file"
          className="form-control mb-2"
          accept="image/*"
          onChange={handleFileChange}
        />
        {imagePreviewUrl && (
          <img src={imagePreviewUrl} alt="Preview" style={{ maxWidth: '150px', maxHeight: '150px', objectFit: 'cover' }} />
        )}
      </div>
    </div>
  );
};

export default ImageUploader;